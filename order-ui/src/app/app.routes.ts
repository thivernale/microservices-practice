import { Routes } from '@angular/router';
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
