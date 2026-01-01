import {ApplicationConfig, inject, provideAppInitializer, provideZoneChangeDetection} from '@angular/core';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {provideRouter} from '@angular/router';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {providePrimeNG} from 'primeng/config';
import Aura from '@primeng/themes/aura';

import {routes} from './app.routes';
import {ConfigService} from './services/config.service';
import {provideApiConfiguration as provideApiConfigurationProduct} from './services/product/api-configuration';
import {environment} from '../environments/environment';

// rootUrl of API-Gateway service
const API_ROOT_URL = environment.apiUrl;

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({eventCoalescing: true}),
    provideRouter(routes),
    provideHttpClient(withInterceptors([])),
    provideAppInitializer(
      () => {
        return inject(ConfigService).loadConfig();
      }
    ),
    provideApiConfigurationProduct(`${API_ROOT_URL}`),
    provideAnimationsAsync(),
    providePrimeNG({theme: {preset: Aura}}),
  ]
};
