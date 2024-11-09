import { HttpClient } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';

import { environment } from './../../environments/environment';


@Component({
  selector: 'app-personas',
  templateUrl: './personas.component.html',
  styleUrl: './personas.component.scss'
})
export class PersonasComponent {
  //table
  public displayedColumns: string[] = ['id', 'codigo', 'nombre', 'apellidos', 'ci', 'tipoDocumento', 'email', 'grupoClienteId'];
  public personas!: MatTableDataSource<Persona>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  public showNew = false;
  public formCliente: FormGroup;


  constructor(
    private http: HttpClient,
    private fb: FormBuilder) {
  }

  ngOnInit() {
    this.get();
    this.initForm();
  }

  get() {
    var url = environment.baseUrl + 'api/Personas';
    this.http.get<Persona[]>(url).subscribe({
      next: (result) => {
        this.personas = new MatTableDataSource<Persona>(result);
        this.personas.paginator = this.paginator;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  openBlockNew() {
    this.showNew = true;
    this.initForm();
  }


  initForm() {
    this.formCliente = this.fb.group({
      codigo: ['', [Validators.required, Validators.maxLength(10)]],
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      apellidos: ['', [Validators.required, Validators.maxLength(100)]],
      ci: ['', [Validators.required, Validators.maxLength(12)]],
      // nit: ['', [Validators.required, Validators.pattern('^[0-9]+$')]],
      tipoDocumento: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      grupoClienteId: [1],
      nuevo: ['']
    });
  }

  registrar() {
    this.showNew = false;
    const data = this.formCliente.getRawValue()
    console.log('registrar', data);
    var url = environment.baseUrl + 'api/Personas';
    this.http.post<Persona>(url, data).subscribe({
      next: (result) => {
        console.log(result);
        this.get();
      }, error: (error) => console.log(error)
    });
  }

}

interface Persona {
  id: number;
  nombre: string;
  apellidos: string;
  ci: number;
  tipoDocumento: string;
  codigo: string;
  email: string;
  grupoClienteId: string;
}
