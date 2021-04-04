package es.upm.miw.betca_tpv_core.domain.services.utils;

import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import es.upm.miw.betca_tpv_core.domain.model.RgpdType;


public class PdfAgreementBuilder {

    private static final String PATH = "/tpv-pdfs/agreements/";
    private static final String FILE = "agreement-";

    public byte[] generateAgreement(Rgpd rgpd) {
        PdfCoreBuilder pdf = new PdfCoreBuilder(PATH, FILE + rgpd.getMobile());
        pdf.a4Size()
                .head()
                .paragraphEmphasized(rgpd.getRgpdType().name() + " Data Protection Act agreement document")
                .line()
                .paragraph("Al registrarte con nosotros crearemos tu cuenta, credenciales que te permitiran darte de alta y gestionar tu actividad en los diferentes Servicios Online que te ofrecemos. Para ello es imprescindible el tratamiento de tus datos personales.\n" +
                        "Un Servicio Online es cualquier servicio electrónico a disposición de clientes y usuarios del grupo, que exija credenciales de acceso para su utilización. Estamos hablando de servicios accesibles a través de paginas web, aplicaciones móviles o similares que exigen de un registro.\n" +
                        "Porque buscamos la máxima transparencia contigo, a continuación te mostramos la información básica sobre como tratamos tus datos personales al crear Tu Cuenta:\n" +
                        "\t* Delegado de protección de datos. Puedes contactar con el a través de la dirección de correo electrónico: miw@etsisi.upm.es\n")
                .line()
                .paragraph("\t- Alta y gestión integral de cuenta que nos permitirá el control de acceso a los Servicios Online en los que estés registrado, incluida la gestión de incidencias así como canalizar sugerencias, quejas  y consultas -, permitiendo a la empresa conocer la totalidad de Servicios Online en los que te has registrado, pero no la actividad que realices en cada uno de ellos salvo que lo consientas.\n" +
                        "\t- Realizar estudios estadísticos y de satisfacción de nuestros servicios.\n");
        if (rgpd.getRgpdType().ordinal() > RgpdType.BASIC.ordinal()) {
            pdf.paragraph("\t- Visión única de cliente dentro del grupo para el tratamiento de datos y envió de comunicaciones con fines comerciales.\n" +
                    "\t- Tratamiento de datos para finalidades comerciales como cliente único del grupo. Esta finalidad implica realizar estudios de mercado y opinión, y la comunicación de tus datos personales identificativos de contacto nombre, documento identificación personal, teléfono y/o correo electrónico- a otras empresas del grupo- a fin de identificar si mantienes relación con alguna de ellas. En caso de que la mantengas, autorizas a que aquellas empresas del grupo que también tengan datos tuyos, los comuniquen. Se combinara toda tu información disponible dentro del grupo, de forma que le permita disponer de una visión única de tu perfil en tus relaciones con el grupo. Esta información será tratada para realizar segmentaciones y perfiles, y hacerte llegar información y ofertas adaptadas a ti, por medios electrónicos o no, sobre productos y/o servicios relacionados con soluciones energéticas, transporte, movilidad, ayuda a la automoción, seguros, finanzas, ocio, viajes, hogar, deporte, gastronomía, programas de fidelización, medios y servicios de pago, o telecomunicaciones.\n" +
                    "\t- Te informamos, sin embargo, que no obstante no prestes tu consentimiento a la finalidad anterior, puede remitirte informaciones comerciales sobre productos y servicios relacionados con la empresa. La base legitima de este tratamiento es la ejecución de tu solicitud de registro y nuestro propio interés legitimo.\n");
            if (rgpd.getRgpdType().ordinal() > RgpdType.MEDIUM.ordinal()) {
                pdf.paragraph("\t- Cesion y posterior tratamiento de datos por las empresas del grupo con fines comerciales.\n" +
                        "\t- Cesion de todos tus datos personales al resto de empresas del grupo para que estas, a su vez, compartan entre si toda tu información, combinen los datos y los traten en la forma y con las finalidades descritas en el apartado anterior.\n");
            }
        }
        pdf.line()
                .paragraphEmphasized("Firma del usuario " + rgpd.getMobile());
        return pdf.build();
    }

}
