@import hexlet.code.page.UrlsPage
@import hexlet.code.util.NamedRoutes
@import java.time.format.DateTimeFormatter
@param UrlsPage page

!{var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");}

@template.layout.page(
page = page,
content = @`
    <div class="container mt-4">
        <h1 class="mb-4">Сайты</h1>

        <table class="table table-striped">
            <thead>
            <tr>
                <th>ID</th>
                <th>Имя сайта</th>
                <th>Последняя проверка</th>
                <th>Статус код</th>
            </tr>
            </thead>
            <tbody>
            @for(var url : page.getUrls())
                !{var urlCheck = page.getLatestChecks().get(url.id());}

                <tr>
                    <td class="fw-bold">${url.id()}</td>
                    <td>
                        <a href="${NamedRoutes.urlPath(url.id())}" class="text-primary">
                            ${url.name()}
                        </a>
                    </td>
                    <td>
                        @if(urlCheck != null)
                            ${urlCheck.createdAt().format(formatter)}
                        @else
                            <span class="text-muted">—</span>
                        @endif
                    </td>
                    <td>
                        @if(urlCheck != null)
                            <span class="badge bg-secondary">${urlCheck.statusCode()}</span>
                        @else
                            <span class="text-muted">—</span>
                        @endif
                    </td>
                </tr>
            @endfor
            </tbody>
        </table>
    </div>
`
)
