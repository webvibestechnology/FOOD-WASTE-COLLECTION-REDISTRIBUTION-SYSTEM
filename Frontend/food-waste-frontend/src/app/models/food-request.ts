export interface FoodRequest {
  id: number;
  donationId: number;
  donationTitle: string;
  ngoId: number;
  ngoName: string;
  status: string;
  notes?: string;
  requestedAt: string;
}