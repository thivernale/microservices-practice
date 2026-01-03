import { provideHttpClient, withInterceptors } from '@angular/common/http';
import {
  ApplicationConfig,
  ErrorHandler,
  inject,
  provideAppInitializer,
  provideZoneChangeDetection
} from '@angular/core';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideRouter } from '@angular/router';
import Aura from '@primeng/themes/aura';
import { MessageService } from 'primeng/api';
import { providePrimeNG } from 'primeng/config';

import { environment } from '../environments/environment';
import { routes } from './app.routes';
import { errorInterceptor } from './core/error.interceptor';
import { GlobalErrorHandler } from './core/global-error-handler';
import { provideApiConfiguration as provideApiConfigurationProduct } from './services/product/api-configuration';
import { ConfigService } from './utils/config/config.service';
import { keycloakInterceptor } from './utils/http/keycloak.interceptor';
import { KeycloakService } from './utils/keycloak/keycloak.service';
import { loadingInterceptor } from './utils/loading/loading.interceptor';

// rootUrl of API-Gateway service
const API_ROOT_URL = environment.apiUrl;

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideHttpClient(
      withInterceptors([
        loadingInterceptor,
        errorInterceptor,
        keycloakInterceptor
      ])
    ),
    MessageService,
    { provide: ErrorHandler, useClass: GlobalErrorHandler },
    provideRouter(routes),
    provideAppInitializer(
      async () => {
        inject(ConfigService).loadConfig();
        await inject(KeycloakService).init();
      }
    ),
    provideAnimationsAsync(),
    providePrimeNG({ theme: { preset: Aura } }),
    provideApiConfigurationProduct(`${API_ROOT_URL}`),
  ]
};
