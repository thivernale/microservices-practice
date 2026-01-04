import { Component, OnDestroy, OnInit } from '@angular/core';
import { ButtonDirective } from 'primeng/button';
import { Ripple } from 'primeng/ripple';
import { Toolbar } from 'primeng/toolbar';
import { Subscription } from 'rxjs';

import { ProductListComponent } from '../../components/product-list/product-list.component';
import { ProductControllerService } from '../../services/product/services/product-controller.service';
import { KeycloakService } from '../../utils/keycloak/keycloak.service';

@Component({
  selector: 'app-main',
  imports: [
    Toolbar,
    ButtonDirective,
    ProductListComponent,
    Ripple
  ],
  templateUrl: './main.component.html',
})
export class MainComponent implements OnInit, OnDestroy {
  protected products: Array<Record<string, any>> | undefined | null;
  private sub: Subscription = new Subscription();

  constructor(
    private readonly searchService: ProductControllerService,
    protected readonly keycloakService: KeycloakService,
  ) {
  }

  ngOnInit() {
    this.sub = this.search().subscribe({
      next: response => {
        this.products = [...response];
      },
      error: err => {
        this.products = [];
        this.products.push(
          {
            id: 1,
            name: 'Sample Product 1',
            description: 'This is a sample product used as fallback data.',
            price: 19.99,
            imageUrl: 'https://via.placeholder.com/150'
          },
          {
            id: 2,
            name: 'Sample Product 2',
            description: 'This is another sample product used as fallback data.',
            price: 29.99,
            imageUrl: 'https://via.placeholder.com/150'
          }
        )
        throw err;
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
    return this.searchService.getAllProducts();
  }
}
