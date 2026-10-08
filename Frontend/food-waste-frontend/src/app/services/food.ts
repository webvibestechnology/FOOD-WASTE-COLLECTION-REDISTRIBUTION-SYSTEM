import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class Food {

  private baseUrl = 'http://localhost:8080/api/food';

  constructor(private http: HttpClient) {}

  getFoodItems(donationId: number): Observable<any> {
    return this.http.get<any>(
      `${this.baseUrl}?donationId=${donationId}`
    );
  }
}