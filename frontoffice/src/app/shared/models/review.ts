export interface Review {
  id: string;
  flightNumber: string;
  destination: string;
  origin: string;
  flightDate?: string;
  rating: number;
  description: string;
  state: string;
  responseText?: string;
}