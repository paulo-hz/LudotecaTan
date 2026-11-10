import { Customer } from '../../customer/model/Customer';
import { Game } from '../../game/model/game';

export interface Loan {
  id: number;
  game: Game;
  customer: Customer;
  startDate: string;
  endDate: string; 
}

export interface LoanSave {
  id: number | null;
  game: { id: number };
  customer: { id: number };
  startDate: string;
  endDate: string;
}
