1. Crea un proyecto Spring Boot con las dependencias de Spring Web
2. Crea el controlador de FunkosRestController con las operaciones CRUD para productos (GET, POST, PUT, PATCH, DELETE) que devuelvan un mensaje de texto con cada operación.
3. Crea el modelo Funko con los siguientes atributos: id, nombre, precio, cantidad, imagen, categoría, fecha de creación y fecha de actualización.
4. Crea el repositorio de Funkos en base a la colección que quieras. Puedes importarla desde un fichero csv que se lea de properties o desde un fichero json, como en ejercicios anteriores.
5. Inyecta el repositorio el controlador de Funkos con las siguientes rutas:
   * GET /funkos: Devuelve todos los funkos, si tiene el query categoría, los filtra por categoría, por ejemplo /funkos?categoria=disney (cuidado con los letras en mayúscula o minúscula)
   * GET /funkos/{id}: Devuelve el funko con el id indicado, si no existe devuelve un error 404
   * POST /funkos: Crea un nuevo funko y lo devuelva
   * PUT /funkos/{id}: Actualiza el funko con el id indicado y lo devuelve, si no existe devuelve un error 404
   * PATCH /funkos/{id}: Actualiza el funko con el id indicado y lo devuelve, si no existe devuelve un error 404
   * DELETE /funkos/{id}: Borra el funko con el id indicado y devuelve un mensaje de éxito, si no existe devuelve un error 404
6. Prueba las rutas con Postman
