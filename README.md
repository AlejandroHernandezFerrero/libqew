# libqew

Librería que proporciona soporte para el diseño de interfaces gráﬁcas de usuario, tanto desde la vista de codiﬁcación como desde la vista de diseño de un IDE. Esta librería permite diseñar las GUIs modularmente en fragmentos e integrarlos posteriormente, de forma que se favorece la reutilización, y sobre todo hace posible diseñar únicamente el fragmento de GUI correspondiente a las novedades añadidas cuando se extienda una clase para la que ya se haya diseñado una GUI, así como reutilizar las GUIs de los objetos de los que se compone una clase para formar su GUI.

Ofrece tres formas de integrar estos fragmentos: en una sola página, en pestañas y en vista de árbol; así como mecanismos que pueden servir para simpliﬁcar la tarea de diseño de GUIs, tales como envolver automáticamente una GUI en una ventana con botones para aceptar o cancelar su contenido y un sistema de paso de mensajes entre GUIs.

Cuenta también con un plugin de NetBeans, que facilita y automatiza su uso en dicho IDE.

Se adjuntan además dos programas de prueba que ejemplifican su uso. *TestB* es un pequeño programa cuya única finalidad es mostrar el funcionamiento de los principales aspectos de la librería y su correcto funcionamiento. Por otra parte, *Snake* es un juego completamente funcional que hace uso extensivo de la librería y muestra su utilidad en un caso de uso realista.
