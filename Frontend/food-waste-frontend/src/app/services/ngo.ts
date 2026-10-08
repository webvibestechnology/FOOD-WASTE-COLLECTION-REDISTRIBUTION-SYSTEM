import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class Ngo {

  private baseUrl = 'http://localhost:8080/api/ngos';

  constructor(private http: HttpClient) {}

  registerAsNgo(data: any): Observable<any> {
    return this.http.post<any>(
      `${this.baseUrl}/register`,
      data
    );
  }

  getMyNgoProfile(): Observable<any> {
    return this.http.get<any>(
      `${this.baseUrl}/profile`
    );
  }

  updateNgoProfile(data: any): Observable<any> {
    return this.http.put<any>(
      `${this.baseUrl}/profile`,
      data
    );
  }

  getAllNgos(): Observable<any> {
    return this.http.get<any>(
      this.baseUrl
    );
  }

  getVerifiedNgos(): Observable<any> {
    return this.http.get<any>(
      `${this.baseUrl}/verified`
    );
  }
}