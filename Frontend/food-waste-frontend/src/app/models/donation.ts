
export interface Donation {
  id: number;
  title: string;
  description?: string;
  quantity: number;
  quantityUnit: string;
  category: string;
  status: string;
  expiryTime: string;
  donorId: number;
  donorName: string;
  createdAt: string;
}