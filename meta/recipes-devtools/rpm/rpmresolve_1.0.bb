SUMMARY = "OpenEmbedded RPM resolver utility"
DESCRIPTION = "OpenEmbedded RPM resolver - performs RPM database lookups in batches to avoid \
 repeated invocations of rpm on the command line."
DEPENDS = "rpm"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"
PR = "r2"

SRC_URI = "file://rpmresolve.c"

S = "${WORKDIR}"

do_compile() {
	${CC} ${CFLAGS} -ggdb -I${STAGING_INCDIR}/rpm ${LDFLAGS} rpmresolve.c -o rpmresolve -lrpmbuild -lrpm -lrpmio -lrpmdb
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 rpmresolve ${D}${bindir}
}

def bindir_native_relpath(d, varname):
    return os.path.relpath(d.getVar(varname, True), d.getVar('bindir_native', True))

native_bindir_to_libdir = "${@bindir_native_relpath(d, 'libdir_native')}"
native_bindir_to_etcdir = "${@bindir_native_relpath(d, 'sysconfdir_native')}"
native_bindir_to_datadir = "${@bindir_native_relpath(d, 'datadir_native')}"
do_install_append_class-native() {
	create_wrapper ${D}/${bindir}/rpmresolve \
			'RPM_USRLIBRPM=$(realpath "$(dirname "$realpath")/${native_bindir_to_libdir}")/rpm' \
			'RPM_ETCRPM=$(realpath "$(dirname "$realpath")/${native_bindir_to_etcdir}")/rpm' \
			'RPM_LOCALEDIRRPM=$(realpath "$(dirname "$realpath")/${native_bindir_to_datadir}")/locale'
}

BBCLASSEXTEND = "native nativesdk"
