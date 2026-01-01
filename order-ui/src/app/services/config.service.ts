import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {tap} from 'rxjs';

export interface AppConfig {
  apiUrl: string;
  enableBetaFeatures: boolean;
  featureToggles: {
    [key: string]: boolean;
  };
}

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  private config: AppConfig = {} as AppConfig;

  constructor(private readonly http: HttpClient) {
  }

  loadConfig() {
    return this.http.get<AppConfig>('/config.json').pipe(
      tap(config => {
          this.config = config;
        }
      )
    );
  }

  getConfig(): AppConfig {
    return this.config;
  }
}
