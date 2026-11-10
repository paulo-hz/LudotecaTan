import { Component, OnInit, inject } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Category } from '../model/category';
import { CategoryService } from '../category.service';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { CategoryEditComponent } from '../category-edit/category-edit.component';
import { DialogConfirmationComponent } from '../../core/dialog-confirmation/dialog-confirmation.component';


@Component({
    selector: 'app-category-list',
    standalone: true,
    imports: [
        MatButtonModule,
        MatIconModule,
        MatTableModule,
        CommonModule
    ],
    templateUrl: './category-list.component.html',
    styleUrl: './category-list.component.css'
})
export class CategoryListComponent implements OnInit{

    dataSource = new MatTableDataSource<Category>();
    displayedColumns: string[] = ['id', 'name', 'action'];

    protected readonly categoryService = inject(CategoryService);
    protected readonly dialog = inject(MatDialog);

    constructor( ) { }

    ngOnInit(): void {
        this.loadData();
    }

    loadData(): void {
        this.categoryService.getCategories().subscribe(
            categories => this.dataSource.data = categories
        );
    }
    
    createCategory() {    
        const dialogRef = this.dialog.open(CategoryEditComponent, {
          data: {}
        });

        dialogRef.afterClosed().subscribe(result => {
        if(!result) return;
        this.loadData();
        });    
    }  

    editCategory(category: Category) {
        const dialogRef = this.dialog.open(CategoryEditComponent, {
        data: { category }
        });

        dialogRef.afterClosed().subscribe(result => {
        if(!result) return;
        this.loadData();
        });
    }

     deleteCategory(category: Category) {    
    const dialogRef = this.dialog.open(DialogConfirmationComponent, {
      data: { title: "Eliminar categoría", description: "Atención si borra la categoría se perderán sus datos.<br> ¿Desea eliminar la categoría?" }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.categoryService.deleteCategory(category.id).subscribe(result => {
          this.loadData();
        }); 
      }
    });
  }  

}



