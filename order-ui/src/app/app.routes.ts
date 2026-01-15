import { Routes } from '@angular/router';
import { IntervalTimerComponent } from './components/interval-timer/interval-timer.component';
import { MainComponent } from './pages/main/main.component';

export const routes: Routes = [
  {
    path: '',
    component: MainComponent
  },
  {
    path: 'interval-timer',
    pathMatch: "full",
    component: IntervalTimerComponent
  }
];
