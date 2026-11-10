import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { CustomerService } from '../customer.service';
import { Customer } from '../model/Customer';
import { CustomerEditComponent } from '../customer-edit/customer-edit.component';
import { DialogConfirmationComponent } from '../../core/dialog-confirmation/dialog-confirmation.component';

@Component({
  selector: 'app-customer-list',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, MatTableModule, CommonModule],
  templateUrl: './customer-list.component.html',
  styleUrl: './customer-list.component.scss',
})
export class CustomerListComponent implements OnInit {
  dataSource = new MatTableDataSource<Customer>();
  displayedColumns: string[] = ['id', 'name', 'action'];

  protected readonly customerService = inject(CustomerService);
  protected readonly dialog = inject(MatDialog);

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.customerService
      .getCustomers()
      .subscribe((customers) => (this.dataSource.data = customers));
  }

  createCustomer(): void {
    const dialogRef = this.dialog.open(CustomerEditComponent, { data: {} });
    dialogRef.afterClosed().subscribe((result) => {
      if (!result) return;
      this.loadData();
    });
  }

  editCustomer(customer: Customer): void {
    const dialogRef = this.dialog.open(CustomerEditComponent, {
      data: { customer },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (!result) return;
      this.loadData();
    });
  }

  deleteCustomer(customer: Customer): void {
    const dialogRef = this.dialog.open(DialogConfirmationComponent, {
      data: {
        title: 'Eliminar cliente',
        description:
          'Atención si borra el cliente se perderán sus datos.<br> ¿Desea eliminar el cliente?',
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (!result) return;
      this.customerService.deleteCustomer(customer.id).subscribe({
        next: () => this.loadData(),
        error: (err) =>
          alert(err.error?.message ?? 'No se ha podido eliminar el cliente'),
      });
    });
  }
}
