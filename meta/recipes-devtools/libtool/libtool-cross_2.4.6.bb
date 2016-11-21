require libtool-${PV}.inc

PACKAGES = ""
SRC_URI += "file://prefix.patch"
SRC_URI += "file://fixinstall.patch"
SRC_URI += "file://support-overriding-lt_sysroot.patch"

datadir = "${WRITE_STAGING_DIR_TARGET}${target_datadir}"

# Move toolchain options from CC/CXX/LD/FC to LT*FLAGS; without this, the
# sysroot used while compiling libtool-cross will be used to infer tagged
# configurations in dependent projects, and tag inferral will fail with
# isolated-sysroots because the dependent component's sysroot differs from
# libtool-cross' one.
export CC = "${CCACHE}${HOST_PREFIX}gcc ${HOST_CC_ARCH}"
export CFLAGS := "${TOOLCHAIN_OPTIONS}${CFLAGS}"
export CXX = "${CCACHE}${HOST_PREFIX}g++ ${HOST_CC_ARCH}"
export CXXFLAGS := "${TOOLCHAIN_OPTIONS}${CXXFLAGS}"
export LD = "${HOST_PREFIX}ld ${HOST_LD_ARCH}"
export LDFLAGS := "${TOOLCHAIN_OPTIONS} ${LDFLAGS}"
export FC = "${CCACHE}${HOST_PREFIX}gfortran ${HOST_CC_ARCH}"

do_configure_prepend () {
	# Remove any existing libtool m4 since old stale versions would break
	# any upgrade
	rm -f ${STAGING_DATADIR}/aclocal/libtool.m4
	rm -f ${STAGING_DATADIR}/aclocal/lt*.m4
}

do_install () {
	install -d ${D}${bindir_crossscripts}/
	install -m 0755 ${HOST_SYS}-libtool ${D}${bindir_crossscripts}/${HOST_SYS}-libtool
	sed -e 's@^\(predep_objects="\).*@\1"@' \
	    -e 's@^\(postdep_objects="\).*@\1"@' \
	    -i ${D}${bindir_crossscripts}/${HOST_SYS}-libtool
	sed -i '/^archive_cmds=/s/\-nostdlib//g' ${D}${bindir_crossscripts}/${HOST_SYS}-libtool
	sed -i '/^archive_expsym_cmds=/s/\-nostdlib//g' ${D}${bindir_crossscripts}/${HOST_SYS}-libtool

	GREP='/bin/grep' SED='sed' ${S}/build-aux/inline-source libtoolize > ${D}${bindir_crossscripts}/libtoolize
	chmod 0755 ${D}${bindir_crossscripts}/libtoolize
	install -d ${D}${target_datadir}/libtool/build-aux/
	install -d ${D}${target_datadir}/aclocal/
	install -c ${S}/build-aux/compile ${D}${target_datadir}/libtool/build-aux/
	install -c ${S}/build-aux/config.guess ${D}${target_datadir}/libtool/build-aux/
	install -c ${S}/build-aux/config.sub ${D}${target_datadir}/libtool/build-aux/
	install -c ${S}/build-aux/depcomp ${D}${target_datadir}/libtool/build-aux/
	install -c ${S}/build-aux/install-sh ${D}${target_datadir}/libtool/build-aux/
	install -c ${S}/build-aux/missing ${D}${target_datadir}/libtool/build-aux/
	install -c -m 0644 ${S}/build-aux/ltmain.sh ${D}${target_datadir}/libtool/build-aux/
	install -c -m 0644 ${S}/m4/*.m4 ${D}${target_datadir}/aclocal/
}

SYSROOT_DIRS += "${bindir_crossscripts} ${target_datadir}"

SSTATE_SCAN_FILES += "libtoolize *-libtool"
