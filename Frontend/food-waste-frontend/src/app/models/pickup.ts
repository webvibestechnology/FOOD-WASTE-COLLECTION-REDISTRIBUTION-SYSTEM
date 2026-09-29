export interface Pickup {
  id: number;
  donationTitle: string;
  volunteerName: string;
  status: string;
  scheduledTime: string;
  completedTime?: string;
}