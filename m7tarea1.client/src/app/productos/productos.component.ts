import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-productos',
  templateUrl: './productos.component.html',
  styleUrl: './productos.component.scss'
})
export class ProductosComponent {
  public productos: Productos[] = [];
  public showNew = false;
  public formRegister: FormGroup;

  constructor(private http: HttpClient,
    private fb: FormBuilder,
  ) { }

  ngOnInit() {
    this.get();
    this.initForm()
  }

  get() {
    this.http.get<Productos[]>('/api/Productos').subscribe(
      (result) => {
        this.productos = result;
      },
      (error) => {
        console.error(error);
      }
    );
  }
  openBlockNew() {
    this.showNew = true;
  }
  registrar() {
    this.showNew = false;
    const data = this.formRegister.getRawValue()
    console.log('registrar', data);
    this.http.post<any>('/api/Productos', data).subscribe(result => {
      console.log(result)
      this.get()
    });

  }
  initForm() {
    this.formRegister = this.fb.group({
      cSku: [null],
      cNombre: [null],
      cNombreExtrangero: [null],
      nPeso: [null],
      cUM: [null],
      nPrecioLista: [null],
      nPrecioBase: [null],
      cCodBarra: [null],
      cSkuFabricante: [null],
      cSkuAlternante: [null],
      grupoProductoId: [null],
      fabricanteId: [null],
      grupoProducto: [null],
      proveedores: [null],
      fabricante: [null],
    });
  }
}

interface Productos {
  id: number;
  cSku: string,
  cNombre: string,
  cNombreExtrangero: string,
  nPeso: number,
  cUM: string,
  nPrecioLista: number,
  nPrecioBase: number,
  cCodBarra: string,
  cSkuFabricante: string,
  cSkuAlternante: string,
  grupoProductoId: number,
  fabricanteId: number,
  grupoProducto: string,
  proveedores: string,
  fabricante: string
}

