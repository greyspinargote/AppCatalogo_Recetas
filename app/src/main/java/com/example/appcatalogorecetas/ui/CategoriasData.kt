package com.example.appcatalogorecetas.ui

data class CategoriaInicio(val emoji: String, val nombreEspanol: String, val nombreIngles: String)
val categoriasInicio = listOf(
    CategoriaInicio("🍝", "Pastas", "Pasta"),
    CategoriaInicio("🍗", "Pollo", "Chicken"),
    CategoriaInicio("🥩", "Res", "Beef"),
    CategoriaInicio("🍰", "Postres", "Dessert"),
    CategoriaInicio("🍤", "Mariscos", "Seafood"),
    CategoriaInicio("🥗", "Vegetariano", "Vegetarian"),
    CategoriaInicio("🥓", "Cerdo", "Pork"),
    CategoriaInicio("🍖", "Cordero", "Lamb"),
    CategoriaInicio("🥞", "Desayuno", "Breakfast"),
    CategoriaInicio("🍲", "Entrada", "Starter"),
    CategoriaInicio("🥦", "Vegano", "Vegan"),
    CategoriaInicio("🍽️", "Acompañamiento", "Side")
)
