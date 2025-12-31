import {ApplicationConfig, provideZoneChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';

import {routes} from './app.routes';
import {providePrimeNG} from 'primeng/config';
import Aura from '@primeng/themes/aura';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {provideApiConfiguration as provideApiConfigurationProduct} from './services/product/api-configuration';

// TODO externalize rootUrl of API-Gateway service
const API_ROOT_URL = 'http://localhost:8080';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({eventCoalescing: true}),
    provideRouter(routes),
    provideHttpClient(withInterceptors([])),
    provideApiConfigurationProduct(`${API_ROOT_URL}`),
    provideAnimationsAsync(),
    providePrimeNG({theme: {preset: Aura}}),
  ]
};
