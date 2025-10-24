@import hexlet.code.dto.MainPage
@import hexlet.code.util.NamedRoutes
@param MainPage page

@template.layout.page(
page = page,
content = @`
    <section class="py-5 bg-light border-bottom">
        <div class="container">
            <div class="row justify-content-center text-center">
                <div class="col-lg-8">
                    <h1 class="display-4 fw-bold mb-3">Проверка сайтов онлайн</h1>
                    <p class="fs-5 text-muted">Узнайте, насколько ваш сайт соответствует SEO‑требованиям</p>
                    <form action="${NamedRoutes.urlsPath()}" method="post" class="mt-4">
                        <div class="row g-2">
                            <div class="col-md-9">
                                <input id="url-input" type="url" name="url" required
                                       class="form-control form-control-lg"
                                       placeholder="https://example.com" aria-label="url">
                            </div>
                            <div class="col-md-3 d-grid">
                                <button type="submit" class="btn btn-lg btn-success">Проверить</button>
                            </div>
                        </div>
                    </form>
                    <p class="mt-3 text-secondary">Например: https://www.example.com</p>
                </div>
            </div>
        </div>
    </section>
`
)
