﻿// <auto-generated />
using M7Tarea1.Server.Data;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;
using Microsoft.EntityFrameworkCore.Metadata;
using Microsoft.EntityFrameworkCore.Migrations;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;

#nullable disable

namespace M7Tarea1.Server.Data.Migrations
{
    [DbContext(typeof(ApplicationDbContext))]
    [Migration("20240921205144_Initial")]
    partial class Initial
    {
        /// <inheritdoc />
        protected override void BuildTargetModel(ModelBuilder modelBuilder)
        {
#pragma warning disable 612, 618
            modelBuilder
                .HasAnnotation("ProductVersion", "8.0.8")
                .HasAnnotation("Relational:MaxIdentifierLength", 128);

            SqlServerModelBuilderExtensions.UseIdentityColumns(modelBuilder);

            modelBuilder.Entity("M7Tarea1.Server.Data.Models.Fabricante", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("Id"));

                    b.Property<string>("cNmbFabricante")
                        .IsRequired()
                        .HasColumnType("nvarchar(100)");

                    b.HasKey("Id");

                    b.ToTable("Fabricantes", (string)null);
                });

            modelBuilder.Entity("M7Tarea1.Server.Data.Models.GrupoProducto", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("Id"));

                    b.Property<string>("cCodGrupoProducto")
                        .IsRequired()
                        .HasColumnType("nvarchar(10)");

                    b.Property<string>("cNombreGrupoProducto")
                        .IsRequired()
                        .HasColumnType("nvarchar(150)");

                    b.HasKey("Id");

                    b.ToTable("GrupoProductos", (string)null);
                });

            modelBuilder.Entity("M7Tarea1.Server.Data.Models.Producto", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("Id"));

                    b.Property<int>("FabricanteId")
                        .HasColumnType("int");

                    b.Property<int>("GrupoProductoId")
                        .HasColumnType("int");

                    b.Property<string>("cCodBarra")
                        .IsRequired()
                        .HasColumnType("nvarchar(50)");

                    b.Property<string>("cNombre")
                        .IsRequired()
                        .HasColumnType("nvarchar(100)");

                    b.Property<string>("cNombreExtrangero")
                        .IsRequired()
                        .HasColumnType("nvarchar(100)");

                    b.Property<string>("cSku")
                        .IsRequired()
                        .HasColumnType("nvarchar(25)");

                    b.Property<string>("cSkuAlternante")
                        .IsRequired()
                        .HasColumnType("nvarchar(25)");

                    b.Property<string>("cSkuFabricante")
                        .IsRequired()
                        .HasColumnType("nvarchar(25)");

                    b.Property<string>("cUM")
                        .IsRequired()
                        .HasColumnType("nvarchar(50)");

                    b.Property<decimal>("nPeso")
                        .HasColumnType("decimal(8,2)");

                    b.Property<decimal>("nPrecioBase")
                        .HasColumnType("decimal(8,2)");

                    b.Property<decimal>("nPrecioLista")
                        .HasColumnType("decimal(8,2)");

                    b.HasKey("Id");

                    b.HasIndex("FabricanteId");

                    b.HasIndex("GrupoProductoId");

                    b.ToTable("Productos", (string)null);
                });

            modelBuilder.Entity("M7Tarea1.Server.Data.Models.Proveedor", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("Id"));

                    b.Property<int>("ProductoId")
                        .HasColumnType("int");

                    b.Property<string>("cNmbProveedor")
                        .IsRequired()
                        .HasColumnType("nvarchar(100)");

                    b.HasKey("Id");

                    b.HasIndex("ProductoId");

                    b.ToTable("Proveedores", (string)null);
                });

            modelBuilder.Entity("M7Tarea1.Server.Data.Models.Producto", b =>
                {
                    b.HasOne("M7Tarea1.Server.Data.Models.Fabricante", "Fabricante")
                        .WithMany("Productos")
                        .HasForeignKey("FabricanteId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("M7Tarea1.Server.Data.Models.GrupoProducto", "GrupoProducto")
                        .WithMany("Productos")
                        .HasForeignKey("GrupoProductoId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("Fabricante");

                    b.Navigation("GrupoProducto");
                });

            modelBuilder.Entity("M7Tarea1.Server.Data.Models.Proveedor", b =>
                {
                    b.HasOne("M7Tarea1.Server.Data.Models.Producto", "Producto")
                        .WithMany("Proveedores")
                        .HasForeignKey("ProductoId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("Producto");
                });

            modelBuilder.Entity("M7Tarea1.Server.Data.Models.Fabricante", b =>
                {
                    b.Navigation("Productos");
                });

            modelBuilder.Entity("M7Tarea1.Server.Data.Models.GrupoProducto", b =>
                {
                    b.Navigation("Productos");
                });

            modelBuilder.Entity("M7Tarea1.Server.Data.Models.Producto", b =>
                {
                    b.Navigation("Proveedores");
                });
#pragma warning restore 612, 618
        }
    }
}
