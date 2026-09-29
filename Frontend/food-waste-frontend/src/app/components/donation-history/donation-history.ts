import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Donation } from '../../services/donation';

@Component({
  selector: 'app-donation-history',
  standalone: true,
  imports: [
    FormsModule,
    DatePipe
  ],
  templateUrl: './donation-history.html',
  styleUrl: './donation-history.css'
})
export class DonationHistoryComponent implements OnInit {

  donations: any[] = [];
  filterStatus: string = 'ALL';

  constructor(
    private donationService: Donation
  ) {}

  ngOnInit(): void {

    this.donationService.getMyDonations().subscribe({

      next: (response) => {
        this.donations = response;
      },

      error: () => {
        this.donations = [];
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