import { isPlatformBrowser } from '@angular/common';
import { Component, Inject, linkedSignal, OnDestroy, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { createLinkedSignal } from '@angular/core/primitives/signals';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ButtonDirective } from 'primeng/button';
import { Ripple } from 'primeng/ripple';
import { Toolbar } from 'primeng/toolbar';
import { delay, Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';

import { ProductListComponent } from '../../components/product-list/product-list.component';
import { ApiFacadeService } from '../../services/api-facade.service';
import { ProductResponse } from '../../services/product/models/product-response';
import { KeycloakService } from '../../utils/keycloak/keycloak.service';

@Component({
  selector: 'app-main',
  imports: [
    Toolbar,
    ButtonDirective,
    ProductListComponent,
    Ripple,
  ],
  templateUrl: './main.component.html',
})
export class MainComponent implements OnInit, OnDestroy {
  protected products: Array<Record<string, any>> | undefined | null;
  protected productsWithSignal = signal<ProductResponse[]>([]).asReadonly();
  protected numProductsSignal = createLinkedSignal(
    () => this.productsWithSignal(),
    (products: ProductResponse[]) => products.length
  );
  protected numProductsWritableSignal = linkedSignal({
    source: () => this.productsWithSignal(),
    computation: (value: ProductResponse[]) => {
      console.log('Num products changed to:', value.length);
      return value.length;
    }
  });
  protected isAuthenticatedSignal = signal<boolean>(false);
  protected isBrowser = signal(false);
  private sub: Subscription = new Subscription();
  private readonly search$;

  constructor(
    private readonly api: ApiFacadeService,
    protected readonly keycloakService: KeycloakService,
    @Inject(PLATFORM_ID) private readonly platformId: Object
  ) {
    this.search$ = this.search()
      .pipe(
        filter(Boolean),
        /* switchMap(products => forkJoin(
            products.map(product => this.api.product.getById({ id: product.id! }))
          )),*/
        delay(1000),
        takeUntilDestroyed()
      );
    this.productsWithSignal = toSignal(this.search$, { initialValue: [] });
    this.isAuthenticatedSignal = this.keycloakService.isAuthenticatedSignal;
    this.isBrowser.set(isPlatformBrowser(this.platformId));
  }

  log() {
    let data = this.numProductsSignal();
    console.log(data);
  }

  ngOnInit() {
    this.sub = this.search$.subscribe({
      next: response => {
        this.products = [...response];
      },
      error: err => {
        this.products = [];
        this.products.push(
          {
            id: 1,
            name: 'Placeholder Product 1',
            description: 'This is a sample product used as fallback data.',
            price: 19.99,
            imageUrl: 'https://via.placeholder.com/150'
          },
          {
            id: 2,
            name: 'Placeholder Product 2',
            description: 'This is another sample product used as fallback data.',
            price: 29.99,
            imageUrl: 'https://via.placeholder.com/150'
          }
        )
        throw err;
      },
      complete: () => {
        console.log('Search completed');
      }
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  protected async toggleAuthenticated() {
    if (this.keycloakService.keycloak.authenticated) {
      await this.keycloakService.logout();
    } else {
      await this.keycloakService.login();
    }
  }

  protected isAuthenticated(): boolean {
    return this.keycloakService.keycloak.authenticated ?? false;
  }

  private search() {
    return this.api.product.getAllProducts();
  }
}
