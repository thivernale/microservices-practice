import { Routes } from '@angular/router';
import { RenderMode, ServerRoute } from '@angular/ssr';
import { IntervalTimerComponent } from './components/interval-timer/interval-timer.component';
import { authGuard } from './core/auth.guard';
import { MainComponent } from './pages/main/main.component';

export const routes: Routes = [
  {
    path: '',
    component: MainComponent
  },
  {
    path: 'checkout',
    loadComponent: () => import('./components/loading-indicator/loading-indicator.component')
      .then(m => m.LoadingIndicatorComponent),
    canActivate: [authGuard],
  },
  {
    path: 'interval-timer',
    pathMatch: "full",
    component: IntervalTimerComponent
  }
];

export const serverRoutes: ServerRoute[] = [
  // render all order-related API routes on the server
  {
    path: '/api/orders/*',
    renderMode: RenderMode.Server
  },
  // prerender the product pages (SSG)
  {
    path: '/api/products/:id',
    renderMode: RenderMode.Prerender,
    getPrerenderParams: async () => {
      const ids = [1, 2, 3]; // Example product IDs to prerender
      // `id` is used in place of `:id` in the route path.
      return ids.map(id => ({ id: id.toString() }));
    }
  },
];
