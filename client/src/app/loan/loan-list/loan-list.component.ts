import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { Pageable } from '../../core/model/page/Pageable';
import { toIsoDate } from '../../core/helpers/date.helper';
import { DialogConfirmationComponent } from '../../core/dialog-confirmation/dialog-confirmation.component';
import { Game } from '../../game/model/game';
import { GameService } from '../../game/game.service';
import { Customer } from '../../customer/model/Customer';
import { CustomerService } from '../../customer/customer.service';
import { Loan } from '../model/Loan';
import { LoanService } from '../loan.service';
import { LoanEditComponent } from '../loan-edit/loan-edit.component';

@Component({
  selector: 'app-loan-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatPaginatorModule,
    MatSelectModule,
    MatTableModule,
  ],
  templateUrl: './loan-list.component.html',
  styleUrl: './loan-list.component.scss',
})
export class LoanListComponent implements OnInit {
  // Datos de los combos de filtro
  protected readonly games = signal<Game[]>([]);
  protected readonly customers = signal<Customer[]>([]);

  // Filtros
  protected readonly filterGameId = signal<number | null>(null);
  protected readonly filterCustomerId = signal<number | null>(null);
  protected readonly filterDate = signal<Date | null>(null);

  // Paginación
  protected readonly pageNumber = signal<number>(0);
  protected readonly pageSize = signal<number>(5);
  protected readonly totalElements = signal<number>(0);

  dataSource = new MatTableDataSource<Loan>();
  displayedColumns: string[] = ['id', 'game', 'customer', 'startDate', 'endDate', 'action'];

  protected readonly loanService = inject(LoanService);
  protected readonly gameService = inject(GameService);
  protected readonly customerService = inject(CustomerService);
  protected readonly dialog = inject(MatDialog);

  ngOnInit(): void {
    this.gameService.getGames().subscribe((games) => this.games.set(games));
    this.customerService
      .getCustomers()
      .subscribe((customers) => this.customers.set(customers));
    this.loadPage();
  }

  loadPage(event?: PageEvent): void {
    if (event) {
      this.pageSize.set(event.pageSize);
      this.pageNumber.set(event.pageIndex);
    }

    const pageable: Pageable = {
      pageNumber: this.pageNumber(),
      pageSize: this.pageSize(),
      sort: [{ property: 'id', direction: 'ASC' }],
    };

    this.loanService
      .getLoans(
        pageable,
        this.filterGameId(),
        this.filterCustomerId(),
        toIsoDate(this.filterDate()),
      )
      .subscribe((data) => {
        this.dataSource.data = data.content;
        this.totalElements.set(data.totalElements);
      });
  }

  onSearch(): void {
    this.pageNumber.set(0);
    this.loadPage();
  }

  onCleanFilter(): void {
    this.filterGameId.set(null);
    this.filterCustomerId.set(null);
    this.filterDate.set(null);
    this.onSearch();
  }

  createLoan(): void {
    const dialogRef = this.dialog.open(LoanEditComponent, { data: {} });
    dialogRef.afterClosed().subscribe((result) => {
      if (!result) return;
      this.loadPage();
    });
  }

  editLoan(loan: Loan): void {
    const dialogRef = this.dialog.open(LoanEditComponent, { data: { loan } });
    dialogRef.afterClosed().subscribe((result) => {
      if (!result) return;
      this.loadPage();
    });
  }

  deleteLoan(loan: Loan): void {
    const dialogRef = this.dialog.open(DialogConfirmationComponent, {
      data: {
        title: 'Eliminar préstamo',
        description:
          'Atención si borra el préstamo se perderán sus datos.<br> ¿Desea eliminar el préstamo?',
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (!result) return;
      this.loanService.deleteLoan(loan.id).subscribe({
        next: () => {
          if (this.dataSource.data.length === 1 && this.pageNumber() > 0) {
            this.pageNumber.set(this.pageNumber() - 1);
          }
          this.loadPage();
        },
        error: (err) =>
          alert(err.error?.message ?? 'No se ha podido eliminar el préstamo'),
      });
    });
  }
}
