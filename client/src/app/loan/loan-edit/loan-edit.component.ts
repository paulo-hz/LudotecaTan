import { Component, OnInit, inject, signal } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { fromIsoDate, toIsoDate } from '../../core/helpers/date.helper';
import { Game } from '../../game/model/game';
import { GameService } from '../../game/game.service';
import { Customer } from '../../customer/model/Customer';
import { CustomerService } from '../../customer/customer.service';
import { Loan, LoanSave } from '../model/Loan';
import { LoanService } from '../loan.service';

const MAX_LOAN_DAYS = 14;
const MS_PER_DAY = 24 * 60 * 60 * 1000;

@Component({
  selector: 'app-loan-edit',
  standalone: true,
  imports: [
    FormsModule,
    MatButtonModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './loan-edit.component.html',
  styleUrl: './loan-edit.component.scss',
})
export class LoanEditComponent implements OnInit {
  protected readonly dialogRef = inject<MatDialogRef<LoanEditComponent>>(MatDialogRef);
  protected readonly data = inject<{ loan?: Loan }>(MAT_DIALOG_DATA);
  protected readonly loanService = inject(LoanService);
  protected readonly gameService = inject(GameService);
  protected readonly customerService = inject(CustomerService);

  protected readonly id = signal<number | null>(null);
  protected readonly customerId = signal<number | null>(null);
  protected readonly gameId = signal<number | null>(null);
  protected readonly startDate = signal<Date | null>(null);
  protected readonly endDate = signal<Date | null>(null);

  protected readonly customers = signal<Customer[]>([]);
  protected readonly games = signal<Game[]>([]);
  protected readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.loadFormData(this.data.loan ?? null);
  }

  loadFormData(initialData: Loan | null): void {
    this.id.set(initialData?.id ?? null);
    this.startDate.set(fromIsoDate(initialData?.startDate));
    this.endDate.set(fromIsoDate(initialData?.endDate));

    this.customerService.getCustomers().subscribe((customers) => {
      this.customers.set(customers);
      this.customerId.set(initialData?.customer?.id ?? null);
    });

    this.gameService.getGames().subscribe((games) => {
      this.games.set(games);
      this.gameId.set(initialData?.game?.id ?? null);
    });
  }

  onSave(): void {
    this.errorMessage.set(null);

    const customerId = this.customerId();
    const gameId = this.gameId();
    const start = this.startDate();
    const end = this.endDate();

    if (!customerId || !gameId || !start || !end) {
      this.errorMessage.set('Todos los campos son obligatorios');
      return;
    }
    if (end < start) {
      this.errorMessage.set('La fecha de fin no puede ser anterior a la fecha de inicio');
      return;
    }
    const days = Math.round((end.getTime() - start.getTime()) / MS_PER_DAY);
    if (days > MAX_LOAN_DAYS) {
      this.errorMessage.set(`El periodo máximo de préstamo es de ${MAX_LOAN_DAYS} días`);
      return;
    }

    const loan: LoanSave = {
      id: this.id(),
      game: { id: gameId },
      customer: { id: customerId },
      startDate: toIsoDate(start)!,
      endDate: toIsoDate(end)!,
    };

    this.loanService.saveLoan(loan).subscribe({
      next: () => this.dialogRef.close(true),
      error: (err) =>
        this.errorMessage.set(err.error?.message ?? 'No se ha podido guardar el préstamo'),
    });
  }

  onClose(): void {
    this.dialogRef.close(false);
  }
}
