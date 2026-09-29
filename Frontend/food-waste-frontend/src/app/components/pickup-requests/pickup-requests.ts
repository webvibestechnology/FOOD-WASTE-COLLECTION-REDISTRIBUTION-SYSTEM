import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Pickup } from '../../services/pickup';

@Component({
  selector: 'app-pickup-requests',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './pickup-requests.html',
  styleUrl: './pickup-requests.css'
})
export class PickupRequests implements OnInit {

  pickups: any[] = [];
  filterStatus = '';

  constructor(
    private pickupService: Pickup
  ) {}

  ngOnInit(): void {
    this.loadPickups();
  }

  loadPickups(): void {
    this.pickupService.getMyPickups().subscribe({
      next: (data: any) => {
        this.pickups = data;
      },
      error: (error: any) => {
        console.error('Error loading pickups:', error);
      }
    });
  }

  get filteredPickups(): any[] {
    if (!this.filterStatus) {
      return this.pickups;
    }

    return this.pickups.filter(
      pickup => pickup.status === this.filterStatus
    );
  }

  updateStatus(id: number, status: string): void {
    this.pickupService.updatePickupStatus(id, status).subscribe({
      next: () => {
        this.loadPickups();
      },
      error: (error: any) => {
        console.error('Error updating pickup:', error);
      }
    });
  }
}