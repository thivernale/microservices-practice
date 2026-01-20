import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Toast } from 'primeng/toast';
import { LoadingIndicatorComponent } from './components/loading-indicator/loading-indicator.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Toast, LoadingIndicatorComponent],
  templateUrl: './app.component.html',
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent {
}
