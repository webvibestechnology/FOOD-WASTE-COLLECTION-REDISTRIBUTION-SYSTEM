import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class Pickup {

  private baseUrl = 'http://localhost:8080/api/pickups';

  constructor(private http: HttpClient) {}

  createPickup(
    donationId: number,
    scheduledTime: string
  ): Observable<any> {
    return this.http.post<any>(
      this.baseUrl,
      {
        donationId,
        scheduledTime
      }
    );
  }

  getMyPickups(): Observable<any> {
    return this.http.get<any>(
      `${this.baseUrl}/my`
    );
  }

  getAllPickups(): Observable<any> {
    return this.http.get<any>(
      this.baseUrl
    );
  }

  updatePickupStatus(
    id: number,
    status: string
  ): Observable<any> {
    return this.http.patch<any>(
      `${this.baseUrl}/${id}/status?status=${status}`,
      {}
    );
  }

  assignVolunteer(
    pickupId: number,
    volunteerId: number
  ): Observable<any> {
    return this.http.patch<any>(
      `${this.baseUrl}/${pickupId}/assign/${volunteerId}`,
      {}
    );
  }
}