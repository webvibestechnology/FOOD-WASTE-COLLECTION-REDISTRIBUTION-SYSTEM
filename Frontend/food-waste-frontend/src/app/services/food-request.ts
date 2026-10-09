import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FoodRequest {

  private baseUrl = 'http://localhost:8080/api/food-requests';

  constructor(private http: HttpClient) {}

  getMyRequests(): Observable<any> {
    return this.http.get<any>(
      `${this.baseUrl}/my`
    );
  }

  createRequest(
    donationId: number,
    message: string
  ): Observable<any> {
    return this.http.post<any>(
      this.baseUrl,
      {
        donationId: donationId,
        message: message
      }
    );
  }
}