import {KeycloakServerConfig} from "keycloak-js";

export interface Environment {
  production: boolean;
  apiUrl: string;
  keycloakConfig: Required<KeycloakServerConfig>;
  redirectUri: Required<string>;
  errorLoggingUrl?: string | null;
}
