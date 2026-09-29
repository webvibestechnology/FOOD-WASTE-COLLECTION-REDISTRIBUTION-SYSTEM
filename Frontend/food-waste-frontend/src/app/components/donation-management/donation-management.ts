import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Admin } from '../../services/admin';

@Component({
  selector: 'app-donation-management',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './donation-management.html',
  styleUrl: './donation-management.css'
})
export class DonationManagement implements OnInit {

  donations: any[] = [];
  filterStatus = 'ALL';

  constructor(private adminService: Admin) {}

  ngOnInit(): void {
    this.loadDonations();
  }

  loadDonations(): void {
    this.adminService.getAllDonations().subscribe({
      next: (data: any) => {
        this.donations = data;
      },
      error: (error: any) => {
        console.error('Error loading donations:', error);
      }
    });
  }

  get filteredDonations(): any[] {
    if (this.filterStatus === 'ALL') {
      return this.donations;
    }

    return this.donations.filter(
      donation => donation.status === this.filterStatus
    );
  }
}