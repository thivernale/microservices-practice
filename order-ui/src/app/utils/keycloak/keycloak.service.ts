import { isPlatformServer } from '@angular/common';
import { inject, Injectable, PLATFORM_ID } from '@angular/core';
import Keycloak from 'keycloak-js';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {
  constructor() {
  }

  private _keycloak: Keycloak | undefined;

  get keycloak(): Keycloak {
    this._keycloak ??= new Keycloak(environment.keycloakConfig);
    return this._keycloak;
  }

  set keycloak(value: Keycloak) {
    this._keycloak = value;
  }

  get userId(): string {
    return this.keycloak.tokenParsed?.sub as string;
  }

  get isTokenValid(): boolean {
    return this.keycloak.isTokenExpired();
  }

  get fullName(): string {
    return this.keycloak.tokenParsed?.["name"] as string;
  }

  async init(): Promise<void> {
    const platformId = inject(PLATFORM_ID);
    if (isPlatformServer(platformId)) {
      return;
    }

    const authenticated = await this.keycloak.init({
      onLoad: 'check-sso',
      checkLoginIframe: false
    });
    if (authenticated) {
      console.log('Keycloak initialized and user authenticated');
    } else {
      console.warn('Keycloak initialization failed or user not authenticated');
    }
  }

  login(): Promise<void> {
    return this.keycloak.login();
  }

  logout(): Promise<void> {
    return this.keycloak.logout({
      redirectUri: environment.redirectUri,
    });
  }

  accountManagement(): Promise<void> {
    return this.keycloak.accountManagement();
  }
}
