@import hexlet.code.page.UrlPage
@import hexlet.code.util.NamedRoutes
@import java.time.format.DateTimeFormatter
@param UrlPage page

!{var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");}

@template.layout.page(
page = page,
content = @`
    <div class="container-lg mt-5">
        <h1 class="mb-4">Сайт: ${page.getUrl().name()}</h1>

        <div class="card mb-4">
            <div class="card-header">
                <h2 class="h5 mb-0">Информация о сайте</h2>
            </div>
            <div class="card-body">
                <dl class="row mb-0">
                    <dt class="col-sm-2">ID</dt>
                    <dd class="col-sm-10">${page.getUrl().id()}</dd>

                    <dt class="col-sm-2">URL</dt>
                    <dd class="col-sm-10">${page.getUrl().name()}</dd>

                    <dt class="col-sm-2">Создан</dt>
                    <dd class="col-sm-10">${page.getUrl().createdAt().format(formatter)}</dd>
                </dl>
            </div>
        </div>

        <div class="d-flex justify-content-between align-items-center mb-3">
            <h2 class="h4 mb-0">Проверки</h2>
            <form method="post" action="${NamedRoutes.urlChecksPath(page.getUrl().id())}">
                <button type="submit" class="btn btn-success">Запустить проверку</button>
            </form>
        </div>

        <table class="table table-striped">
            <thead class="table-light">
            <tr>
                <th>ID</th>
                <th>Код</th>
                <th>Заголовок</th>
                <th>H1</th>
                <th>Описание</th>
                <th>Дата проверки</th>
            </tr>
            </thead>
            <tbody>
            @for(var check : page.getChecks())
                <tr>
                    <td class="fw-bold">${check.id()}</td>
                    <td>
                        <span class="badge bg-secondary">${check.statusCode()}</span>
                    </td>
                    <td>${check.title()}</td>
                    <td>${check.h1()}</td>
                    <td>${check.description()}</td>
                    <td>${check.createdAt().format(formatter)}</td>
                </tr>
            @endfor
            </tbody>
        </table>
    </div>
`
)
