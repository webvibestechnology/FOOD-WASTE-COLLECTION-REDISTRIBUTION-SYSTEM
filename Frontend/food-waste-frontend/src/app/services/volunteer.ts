import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class Volunteer {

  private baseUrl = 'http://localhost:8080/api/volunteers';

  constructor(private http: HttpClient) {}

  registerAsVolunteer(data: any): Observable<any> {
    return this.http.post<any>(
      `${this.baseUrl}/register`,
      data
    );
  }

  getMyVolunteerProfile(): Observable<any> {
    return this.http.get<any>(
      `${this.baseUrl}/profile`
    );
  }

  updateProfile(data: any): Observable<any> {
    return this.http.put<any>(
      `${this.baseUrl}/profile`,
      data
    );
  }

  toggleAvailability(): Observable<any> {
    return this.http.patch<any>(
      `${this.baseUrl}/availability`,
      {}
    );
  }

  getAvailableVolunteers(): Observable<any> {
    return this.http.get<any>(
      `${this.baseUrl}/available`
    );
  }
}