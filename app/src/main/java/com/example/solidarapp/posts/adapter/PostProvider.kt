package com.example.solidarapp.posts.adapter

import com.example.solidarapp.posts.adapter.Post

class PostProvider {
    companion object{
        val PostList = listOf<Post>(
            Post (
                postID = 999,
                "example@example.com",
            "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?q=80&w=2080&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "Frank Toe",
            "30 de Noviembre del 2023",
            "https://www.diariodexalapa.com.mx/local/f1fkzw-centros-de-acopio-para-damnificados-por-otis-en-veracruz-boca-del-rio-sedes/ALTERNATES/LANDSCAPE_768/Centros%20de%20acopio%20para%20damnificados%20por%20Otis%20en%20Veracruz-Boca%20del%20R%C3%ADo;%20sedes",
            "A 3 personas les gusta esto.",
            "Arrancan los Centros de Acopio para Damnificados por Otis.",
            "example@example.com"),
            Post (
                postID = 1000,
                "example@example.com",
                "https://images.unsplash.com/photo-1633332755192-727a05c4013d?q=80&w=2080&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "James Williams",
                "1 de Diciembre del 2023",
                "https://coedupia.com/wp-content/uploads/2020/02/IMG-20191005-WA0007_1024x768-1024x675.jpg",
                "A 1 persona le gusta esto.",
                "Presentamos los integrantes del Centro Social Calasanz.",
                "example@example.com")
        )

    }
}