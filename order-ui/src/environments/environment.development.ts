import {Environment} from '../types/environment';

export const environment: Environment = {
  production: false,
  apiUrl: 'http://localhost:8080',
  keycloakConfig: {
    url: 'http://localhost:8180',
    realm: 'spring-boot-microservices-realm',
    clientId: 'order-ui-client',
  },
  redirectUri: 'http://localhost:4200',
  errorLoggingUrl: null
};
