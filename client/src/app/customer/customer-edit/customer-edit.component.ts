import { Component, OnInit, inject, signal } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { CustomerService } from '../customer.service';
import { Customer } from '../model/Customer';

@Component({
  selector: 'app-customer-edit',
  standalone: true,
  imports: [FormsModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './customer-edit.component.html',
  styleUrl: './customer-edit.component.scss',
})
export class CustomerEditComponent implements OnInit {
  protected readonly dialogRef = inject<MatDialogRef<CustomerEditComponent>>(MatDialogRef);
  protected readonly data = inject<{ customer?: Customer }>(MAT_DIALOG_DATA);
  protected readonly customerService = inject(CustomerService);

  protected readonly id = signal<number | null>(null);
  protected readonly name = signal<string | null>(null);
  protected readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.loadFormData(this.data.customer ?? null);
  }

  loadFormData(initialData: Customer | null): void {
    this.id.set(initialData?.id ?? null);
    this.name.set(initialData?.name ?? null);
  }

  onSave(): void {
    this.errorMessage.set(null);

    const name = this.name()?.trim();
    if (!name) {
      this.errorMessage.set('El nombre no puede estar vacío');
      return;
    }

    const customer = { id: this.id(), name } as Customer;
    this.customerService.saveCustomer(customer).subscribe({
      next: () => this.dialogRef.close(true),
      error: (err) =>
        this.errorMessage.set(
          err.error?.message ?? 'No se ha podido guardar el cliente',
        ),
    });
  }

  onClose(): void {
    this.dialogRef.close(false);
  }
}
