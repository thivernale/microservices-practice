import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { KeycloakService } from '../utils/keycloak/keycloak.service';

export const authGuard: CanActivateFn = (route, state) => {
  return inject(KeycloakService).keycloak.authenticated ?? false;
};
