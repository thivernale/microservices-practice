import { isPlatformServer } from '@angular/common';
import { inject, Injectable, PLATFORM_ID, signal } from '@angular/core';
import Keycloak from 'keycloak-js';
import { environment } from '../../../environments/environment';
import { CustomerRequest } from '../../services/customer/models/customer-request';

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {
  isAuthenticatedSignal = signal<boolean>(false);
  private readonly platformId = inject(PLATFORM_ID);

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

  get customerIdentity(): CustomerRequest | null {
    const token = this.keycloak.tokenParsed as Record<string, unknown> | undefined;
    if (!token) {
      return null;
    }

    const id = this.userId;
    const email = (token['email'] as string) ?? '';
    if (!id || !email) {
      return null;
    }

    const { firstName, lastName } = this.resolveCustomerName(token);

    return {
      id,
      email,
      firstName,
      lastName,
    };
  }

  async init(): Promise<void> {
    if (isPlatformServer(this.platformId)) {
      return;
    }

    await this.pingKeycloakServer();

    const authenticated = await this.keycloak.init({
      onLoad: 'check-sso',
      checkLoginIframe: false
    });

    this.isAuthenticatedSignal.set(authenticated);

    if (authenticated) {
      console.log('Keycloak initialized and user authenticated');
    } else {
      console.warn('Keycloak initialization failed or user not authenticated');
    }
  }

  login(): Promise<void> {
    if (!this.keycloak.didInitialize) {
      return this.init().then(() => this.keycloak.login());
    }
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

  private async pingKeycloakServer() {
    // ping url to see if server is reachable
    try {
      await fetch(
        `${environment.keycloakConfig.url}/realms/${environment.keycloakConfig.realm}`,
        { method: 'HEAD', mode: 'no-cors' }
      );
    } catch (error) {
      throw new Error('Error reaching Keycloak server: ' + (error as Error)?.message, { cause: error as Error });
    }
  }

  private resolveCustomerName(token: Record<string, unknown>): { firstName: string; lastName: string } {
    const givenName = token['given_name'] as string | undefined;
    const familyName = token['family_name'] as string | undefined;
    let firstName = givenName ?? '';
    let lastName = familyName ?? '';

    if (!firstName || !lastName) {
      const fullName = (token['name'] as string) ?? '';
      if (fullName) {
        const parts = fullName.trim().split(/\s+/);
        if (!firstName && parts.length > 0) {
          firstName = parts[0];
        }
        if (!lastName && parts.length > 1) {
          lastName = parts.slice(1).join(' ');
        }
      }
    }

    if (!firstName) {
      firstName = (token['preferred_username'] as string) ?? 'User';
    }

    return { firstName, lastName };
  }
}
