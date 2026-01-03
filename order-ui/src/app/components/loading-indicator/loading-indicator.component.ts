import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import { Component, ContentChild, Input, OnInit, TemplateRef } from '@angular/core';
import { NavigationStart, RouteConfigLoadStart, Router } from '@angular/router';
import { BlockUI } from 'primeng/blockui';
import { ProgressSpinner } from 'primeng/progressspinner';
import { Observable, tap } from 'rxjs';
import { LoadingService } from '../../utils/loading/loading.service';

@Component({
  selector: 'app-loading-indicator',
  imports: [
    ProgressSpinner,
    BlockUI,
    AsyncPipe,
    NgTemplateOutlet
  ],
  templateUrl: './loading-indicator.component.html',
})
export class LoadingIndicatorComponent implements OnInit {
  @Input()
  detectRouteTransitions = false;

  @ContentChild("loading")
  customLoadingIndicator: TemplateRef<any> | null = null;

  protected loading$: Observable<boolean>;

  constructor(
    private readonly loadingService: LoadingService,
    private readonly router: Router,
  ) {
    this.loading$ = this.loadingService.loading$;
  }

  ngOnInit(): void {
    if (this.detectRouteTransitions) {
      this.router.events.pipe(tap((event) => {
        if (event instanceof RouteConfigLoadStart) {
          this.loadingService.show();
        } else if (event instanceof NavigationStart) {
          this.loadingService.hide();
        }
      })).subscribe();
    }
  }
}
