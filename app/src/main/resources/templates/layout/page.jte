@import gg.jte.Content
@import hexlet.code.page.BasePage
@import hexlet.code.util.NamedRoutes
@param Content content
@param BasePage page = null

<!doctype html>
<html lang="ru">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Анализатор страниц</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
              crossorigin="anonymous">
    </head>

    <body class="d-flex flex-column min-vh-100 bg-light">
        <header>
            <nav class="navbar navbar-expand-md navbar-dark bg-primary">
                <div class="container">
                    <a class="navbar-brand fw-bold" href="${NamedRoutes.rootPath()}">Анализатор страниц</a>
                    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav"
                            aria-controls="mainNav" aria-expanded="false" aria-label="Toggle navigation">
                        <span class="navbar-toggler-icon"></span>
                    </button>
                    <div id="mainNav" class="collapse navbar-collapse">
                        <ul class="navbar-nav">
                            <li class="nav-item"><a class="nav-link" href="${NamedRoutes.rootPath()}">Главная</a></li>
                            <li class="nav-item"><a class="nav-link" href="${NamedRoutes.urlsPath()}">Сайты</a></li>
                        </ul>
                    </div>
                </div>
            </nav>
        </header>

        <main class="flex-grow-1 container py-4">
            @if(page != null && page.getFlash() != null)
                <div class="alert alert-${page.getFlashType()} alert-dismissible fade show" role="alert">
                    ${page.getFlash()}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Закрыть"></button>
                </div>
            @endif
            ${content}
        </main>

        <footer class="mt-auto border-top bg-white py-3">
            <div class="container text-center text-muted">
                <small>© ${String.valueOf(java.time.Year.now())} — учебный проект. Сделано с помощью
                    <a href="https://ru.hexlet.io" target="_blank" class="link-secondary">Hexlet</a>
                </small>
            </div>
        </footer>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"
                crossorigin="anonymous">
        </script>
    </body>
</html>
