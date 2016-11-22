require python-scons_${PV}.bb
inherit native pythonnative
DEPENDS = "python-native"
RDEPENDS_${PN} = ""

bindir_to_python_sitepackages = "${@os.path.relpath(d.getVar('PYTHON_SITEPACKAGES_DIR', True), d.getVar('bindir', True))}"
do_install_append() {
    create_wrapper ${D}${bindir}/scons SCONS_LIB_DIR='$(dirname "$realpath")/${bindir_to_python_sitepackages}'
}
