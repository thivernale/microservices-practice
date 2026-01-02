import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { KeycloakService } from '../keycloak/keycloak.service';

export const keycloakInterceptor: HttpInterceptorFn = (req, next) => {
  const keycloak = inject(KeycloakService).keycloak;
  const token = keycloak.token;

  if (token && !keycloak.isTokenExpired()) {
    const clonedReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(clonedReq);
  }

  return next(req);
};
