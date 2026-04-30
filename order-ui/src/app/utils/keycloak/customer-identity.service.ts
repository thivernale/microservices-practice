import { isPlatformServer } from '@angular/common';
import { inject, Injectable, PLATFORM_ID } from '@angular/core';
import { firstValueFrom } from 'rxjs';

import { ApiFacadeService } from '../../services/api-facade.service';
import { KeycloakService } from './keycloak.service';

@Injectable({
  providedIn: 'root'
})
export class CustomerIdentityService {
  private readonly platformId = inject(PLATFORM_ID);
  private readonly api = inject(ApiFacadeService);
  private readonly keycloakService = inject(KeycloakService);

  async syncCustomerIdentity(): Promise<void> {
    if (isPlatformServer(this.platformId)) {
      return;
    }

    if (!this.keycloakService.keycloak.authenticated) {
      return;
    }

    const identity = this.keycloakService.customerIdentity;
    if (!identity?.id || !identity.email) {
      console.warn('Skipping customer identity sync: missing id or email in token.');
      return;
    }

    try {
      const exists = await firstValueFrom(
        this.api.customer.existsById({ id: identity.id })
      );

      if (exists) {
        console.log('Customer identity already exists in the system.');
        return;
      }

      const customerResponse = await firstValueFrom(
        this.api.customer.createCustomer({ body: identity })
      );
      console.log('Customer identity synced with Keycloak user:', customerResponse);
    } catch (error) {
      console.error('Failed to sync customer identity with Keycloak user.', error);
    }
  }
}
