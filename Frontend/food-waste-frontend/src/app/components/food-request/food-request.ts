import { Component, OnInit } from '@angular/core';
import { FoodRequest } from '../../services/food-request';
import { DatePipe } from '@angular/common';

@Component({
  
  selector: 'app-food-request',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './food-request.html',
  styleUrl: './food-request.css'
})
export class FoodRequestComponent implements OnInit {

  requests: any[] = [];

  constructor(
    private foodRequestService: FoodRequest
  ) {}

  ngOnInit(): void {
    this.loadRequests();
  }

  loadRequests(): void {
    this.foodRequestService.getMyRequests().subscribe({
      next: (data: any) => {
        this.requests = data;
      },
      error: (error: any) => {
        console.error('Error loading requests:', error);
      }
    });
  }
}