import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Pageable } from '../core/model/page/Pageable';
import { PaginatedData } from '../core/model/page/PaginatedData';
import { Loan, LoanSave } from './model/Loan';

@Injectable({
  providedIn: 'root',
})
export class LoanService {
  protected readonly http = inject(HttpClient);

  private baseUrl = 'http://localhost:8080/loan';

  getLoans(
    pageable: Pageable,
    idGame: number | null,
    idCustomer: number | null,
    date: string | null,
  ): Observable<PaginatedData<Loan>> {
    return this.http.post<PaginatedData<Loan>>(this.baseUrl, {
      pageable,
      idGame,
      idCustomer,
      date,
    });
  }

  saveLoan(loan: LoanSave): Observable<void> {
    const { id } = loan;
    const url = id ? `${this.baseUrl}/${id}` : this.baseUrl;
    return this.http.put<void>(url, loan);
  }

  deleteLoan(idLoan: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${idLoan}`);
  }
}
