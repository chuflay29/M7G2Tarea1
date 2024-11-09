using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace M7Tarea1.Server.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class SaludoController : ControllerBase
    {
        [HttpGet]
        public IActionResult GetSaludo()
        {
            return Ok("Hola, mundo");
        }

        [HttpGet("{titulo}")]
        public IActionResult GetSaludo(string titulo)
        {
            return Ok("Hola, " + titulo);
        }
    }
}
