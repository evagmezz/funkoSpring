db = db.getSiblingDB('pedidos');

db.createCollection('pedidos');

db.pedidos.insertMany([
    {
        _id: ObjectId('6536518de9b0d305f193b5ef'),
        idUsuario: 1,
        cliente: {
            nombreCompleto: 'eva gomez',
            email: 'evagomez@gmail.com',
            telefono: '+34123456789',
            direccion: {
                calle: 'Calle Mayor',
                numero: '10',
                ciudad: 'Madrid',
                provincia: 'Madrid',
                pais: 'España',
                codigoPostal: '28001',
            },
        },
        lineasPedido: [
            {
                idFunko: 2,
                cantidad: 1,
                precioFunko: 19.99,
                total: 19.99,
            },
            {
                idFunko: 3,
                cantidad: 2,
                precioFunko: 15.99,
                total: 31.98,
            },
        ],
        totalItems: 3,
        total: 51.97,
        createdAt: '2023-11-15T12:57:17.3411925',
        updatedAt: '2023-11-15T12:57:17.3411925',
        isDeleted: false
    }]);