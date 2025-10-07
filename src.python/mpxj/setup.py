import setuptools

with open("README.md", "r", encoding="utf-8") as fh:
    long_description = fh.read()

setuptools.setup(
    name="mpxj",
    version="14.5.1",
    author="Jon Iles",
    author_email="jon@timephased.com",
    description="Python wrapper for the MPXJ Java library for manipulating project files",
    long_description=long_description,
    long_description_content_type="text/markdown",
    url="https://github.com/joniles/mpxj",
    project_urls={
        "Bug Tracker": "https://github.com/joniles/mpxj/issues",
    },
    classifiers=[
        "Programming Language :: Python :: 3",
        "Operating System :: OS Independent",
    ],
    packages=setuptools.find_namespace_packages(),
    python_requires=">=3.6",
    include_package_data=True,
    package_data={'': ['lib/*.jar']},
    license="LGPL-2.0-or-later"
)
