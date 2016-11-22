#
# This class will update the Gdk pixbuf cache before do_configure if isolated
# sysroots are in use
#

do_configure_prepend() {
    # Update the pixbuf loader cache when isolated sysroots are used, because
    # each separate sysroot needs its own cache.
    if [ "${ISOLATED_SYSROOTS}" = "1" ]; then
        bbnote "Updating Gdk pixbuf query loaders cache"
        GDK_PIXBUF_FATAL_LOADER=1 "${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/gdk-pixbuf-query-loaders" --update-cache || exit 1
    fi
}

