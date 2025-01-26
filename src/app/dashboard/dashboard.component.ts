
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private apiUrl = 'https://localhost:8443/api/v1/encryption/sensor';

  constructor(private http: HttpClient) { }

  // pour récupérer les données de sensor avec l'ID égal à 1
  getSensorDataById(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}`);
  }
}
