# Explication de `executeMethode`

La méthode `executeMethode` reçoit une méthode de contrôleur trouvée à partir d'une route HTTP, prépare ses paramètres, l'exécute par réflexion, puis traite sa valeur de retour.

Son déroulement général est le suivant :

1. récupérer la classe qui contient la méthode ;
2. créer une instance de cette classe ;
3. construire automatiquement les arguments de la méthode ;
4. appeler la méthode avec ces arguments ;
5. écrire le résultat dans la réponse HTTP ou transmettre vers une vue.

La partie expliquée ci-dessous correspond à l'étape 3.

## Construction des arguments

Le code suivant prépare les arguments qui seront transmis à une méthode de contrôleur appelée par réflexion :

```java
Object[] arguments = Arrays.stream(method.getParameterTypes())
    .map(type -> resolveArgument(type, req, res, applicationContext))
    .toArray();
```

## 1. Récupérer les types des paramètres

```java
method.getParameterTypes()
```

`method` est un objet Java de type `Method`. Il représente une méthode Java et permet d'obtenir ses informations.

`getParameterTypes()` récupère les types des paramètres de cette méthode.

Par exemple, pour cette méthode :

```java
public String afficher(HttpServletRequest req, UserService service)
```

Java récupère conceptuellement le tableau suivant :

```java
[
    HttpServletRequest.class,
    UserService.class
]
```

## 2. Parcourir les types avec un Stream

```java
Arrays.stream(method.getParameterTypes())
```

`method.getParameterTypes()` renvoie un tableau de types (`Class<?>[]`).

`Arrays.stream(...)` transforme ce tableau en `Stream`, afin de traiter chaque type l'un après l'autre.

## 3. Résoudre la valeur de chaque argument

```java
.map(type -> resolveArgument(type, req, res, applicationContext))
```

Pour chaque type de paramètre, la méthode `resolveArgument` cherche la valeur correspondante.

Elle applique les règles suivantes :

- `ApplicationContext.class` retourne `applicationContext` ;
- `HttpServletRequest.class` retourne `req` ;
- `HttpServletResponse.class` retourne `res` ;
- tout autre type est recherché comme bean Spring avec `applicationContext.getBean(type)`.

Par exemple, pour les paramètres suivants :

```java
HttpServletRequest req, UserService service
```

les valeurs produites seront :

```java
[
    req,
    userService
]
```

## 4. Transformer le Stream en tableau

```java
.toArray()
```

Cette instruction transforme les valeurs produites par le Stream en tableau `Object[]`.

Le résultat est stocké dans la variable :

```java
Object[] arguments
```

## 5. Utiliser les arguments

Le tableau est ensuite transmis à la méthode appelée par réflexion :

```java
Object retour = method.invoke(object, arguments);
```

Cela revient conceptuellement à appeler :

```java
object.afficher(req, userService);
```

La réflexion permet donc d'appeler une méthode sans connaître son nom ni ses paramètres à l'avance.

## Cas particulier

Si la méthode ne possède aucun paramètre :

```java
public String afficher()
```

`method.getParameterTypes()` est vide et `arguments` devient un tableau vide. La méthode sera appelée ainsi :

```java
method.invoke(object, new Object[0]);
```

L'ordre des arguments est toujours conservé : le premier élément correspond au premier paramètre de la méthode, le deuxième élément au deuxième paramètre, et ainsi de suite.

## Retour JSON avec `@ApiRest`

L'annotation `@ApiRest` est détectée dans `FrontControllerServlet` :

```java
if (method.isAnnotationPresent(ApiRest.class)) {
    res.setContentType("application/json;charset=UTF-8");
}
```

Elle indique que le type de contenu HTTP doit être `application/json`. Cependant, elle ne convertit pas elle-même la valeur retournée en JSON.

La conversion est réalisée dans `executeMethode` :

```java
} else if (retour != null) {
    Gson gson = new Gson();
    String json = gson.toJson(retour);
    out.println(json);
}
```

Ainsi, si une méthode annotée `@ApiRest` retourne un objet Java, cet objet est converti en JSON avec Gson.

Exemple :

```java
@ApiRest
public User getUser() {
    return new User(1, "Alice");
}
```

La réponse sera comparable à :

```json
{"id":1,"name":"Alice"}
```

### Cas particuliers

- Si la méthode retourne un `String`, il est écrit directement, sans conversion Gson. Ce n'est donc pas forcément une chaîne JSON valide.
- Si la méthode retourne un `ModelAndView`, une vue HTML est utilisée avec `Dispatcher.dispatch`, même si la méthode est annotée `@ApiRest`.
- Dans `FrontControllerServlet`, le texte `Request Path:...` est écrit avant l'exécution du contrôleur. S'il reste présent, il est ajouté avant le JSON et la réponse complète n'est plus un JSON valide.

En résumé, `@ApiRest` configure le format annoncé par la réponse, mais c'est le type de retour traité par `executeMethode` qui détermine si une conversion JSON est réellement effectuée.
